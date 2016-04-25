from flask import Flask

from flask_restful import Api

app = Flask(__name__)

app.config.from_object('config')
app.config.from_envvar('DOU_SETTINGS', silent=True)


api = Api(app)

from siri import siri_views
