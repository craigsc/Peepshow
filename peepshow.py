#!/usr/bin/env python
import tornado.httpserver
import tornado.httpclient
import tornado.ioloop
import tornado.web
from tornado.options import define, options
import tornado.options
from pymongo import Connection, GEO2D
from tornado.escape import json_encode

define("port", default=3333, type=int, help="port to listen on")
define("radius", default=0.03, type=float, help="distance for each quadrant")

class Application(tornado.web.Application):
	def __init__(self):
		handlers = [
			(r"/send/audio/(.+)/(.+)/([0-9]+\.[0-9]+)/([0-9]+\.[0-9]+)", UpdateAudioHandler),
			(r"/send/web/(.+)/([0-9]+\.[0-9]+)/([0-9]+\.[0-9]+)", UpdateWebHandler),
			(r"/get/([0-9]+\.[0-9]+)/([0-9]+\.[0-9]+)", GetHandler),
		]
		settings = {
			"debug": True,
		}
		tornado.web.Application.__init__(self, handlers, **settings)
		
		self.db = Connection().peepshow
		self.db.peeps.create_index([('gps', GEO2D)])
		
class BaseHandler(tornado.web.RequestHandler):
	@property
	def db(self):
		return self.application.db
			
class GetHandler(BaseHandler):
	def get(self, lat, lon):
		if not lat or not lon:
			self.write('error')
			return
		peeps = []
		for peep in self.db.peeps.find({'gps': {'$within': {"$center": [[float(lat), float(lon)], options.radius]}}}, {'_id': 0}).sort('votes', -1):
			peep['lat'] = peep['gps'][0]
			peep['lon'] = peep['gps'][1]
			del peep['gps']
			peeps.append(peep)
		self.write(json_encode(peeps))

class UpdateAudioHandler(BaseHandler):
	def get(self, artist, title, lat, lon):
		if not artist or not title or not lat or not lon:
			self.write('error')
			return
		peep = self.db.peeps.find_one({'type': 'audio', 'artist': artist, 'gps': {'$within': {"$center": [[float(lat), float(lon)], options.radius]}}, 'title': title}, {'_id': 1})
		if peep:
			self.db.peeps.update({'_id': peep['_id']}, {'$inc': {'votes': 1}})
		else :
			self.db.peeps.insert({'gps': [float(lat), float(lon)], 'artist': artist, 'type': 'audio', 'title': title, 'votes': 1})
		self.write('success')

class UpdateWebHandler(BaseHandler):
	def get(self, url, lat, lon):
		if not url or not lat or not lon:
			self.write('error')
			return
		peep = self.db.peeps.find_one({'type': 'web', 'url': url, 'gps': {'$within': {"$center": [[float(lat), float(lon)], options.radius]}}}, {'_id': 1})
		if peep:
			self.db.peeps.update({'_id': peep['_id']}, {'$inc': {'votes': 1}})
		else:
			self.db.peeps.insert({'type': 'web', 'gps': [float(lat), float(lon)], 'url': url, 'votes': 1})
		self.write('success')
		
if __name__ == "__main__":
	tornado.options.parse_command_line()
	http_server = tornado.httpserver.HTTPServer(Application())
	http_server.listen(options.port)
	tornado.ioloop.IOLoop.instance().start()