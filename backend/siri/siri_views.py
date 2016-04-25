# encoding=utf-8
from SvsR import api, app

from flask_restful import Resource
from flask import request, jsonify
import wikipedia

import requests
from bs4 import BeautifulSoup

import ast
import json
import urllib
import re


def check_contain_chinese(check_str):
    for ch in check_str:
        if u'\u4e00' <= ch <= u'\u9fff':
            return True
    return False


class Wiki(Resource):

    def get(self, keyword):
        if check_contain_chinese(keyword):
            wikipedia.set_lang("zh")
            print("set lang to zh")
        result = wikipedia.summary(keyword)
        print(result)
        return {"result": result}


class BaiduSearch(Resource):
    BaseBaiduUrl = "http://www.baidu.com/s?wd="

    def get(self, keyword):
        content_url = self.BaseBaiduUrl + keyword
        soup = BeautifulSoup(requests.get(content_url).content)
        content_left = soup.find(id="content_left")
        result_list = content_left.findAll("div", {"class": "result-op"})
        # cl[4].find('a')

        d = {}
        d.setdefault('result', [])

        for result in result_list:
            if u"百度百科" in result.find('a').text:
                continue
            d['result'].append(
                {'link': result.find('a').get('href'), 'text': result.find('a').text})

        return d


class Baike(Resource):
    BaseBaiduUrl = "http://www.baidu.com/s?wd="

    def get(self, keyword):
        content_url = self.BaseBaiduUrl + keyword
        soup = BeautifulSoup(requests.get(content_url).content)
        content_left = soup.find(id="content_left")
        result_list = content_left.findAll("div", {"class": "result-op"})
        # cl[4].find('a')

        d = {}
        d.setdefault('result', [])

        s = ""

        for result in result_list:
            if u"百度百科" in result.find('a').text:
                for i in result.findAll('p'):
                    s += i.text
                d["link"] = result.find('a').get('href')
                break
                # d['result'].append({'link': result.find('a').get('href'),
                # 'text': result.find('a').text})
        d["text"] = s
        return d


class BookSearch(Resource):
    BaseBookUrl = "https://api.douban.com/v2/book/search?q="

    def get(self, keyword):
        content_url = self.BaseBookUrl + keyword
        d = requests.get(content_url).json()
        l = d['books']
        o = {}
        if len(l) > 0:
            o['title'] = l[0]['title']
            o['rating'] = l[0]['rating']
            o['author'] = l[0]['author']
            o['link'] = l[0]['alt']
        return o


class News(Resource):
    BaseNewUrl = "http://news.baidu.com/ns?word="

    def get(self, keyword):
        content_url = self.BaseNewUrl + keyword
        content = requests.get(content_url).content
        soup = BeautifulSoup(content)
        content_left = soup.find(id="content_left")
        result_list = content_left.findAll("li", {"class": "result"})

        d = {}
        d.setdefault('result', [])

        for result in result_list:
            d['result'].append(
                {'link': result.find('a').get('href'), 'text': result.find('div', {}).text})

        return d


class MusicSearch(Resource):
    BaseMusicUrl = "https://api.douban.com/v2/music/search?q="

    def get(self, keyword):
        content_url = self.BaseMusicUrl + keyword
        d = requests.get(content_url).json()
        l = d['musics']
        ol = []
        for item in l:
            o = {}
            o['title'] = item['title']
            o['rating'] = item['rating']
            o['link'] = item['mobile_link']
            ol.append(o)
        return ol


class MovieSearch(Resource):
    BaseMovieUrl = "https://api.douban.com/v2/movie/search?q="

    def get(self, keyword):
        content_url = self.BaseMovieUrl + keyword
        d = requests.get(content_url).json()
        o = {}
        l = d['subjects']
        if len(l) > 0:
            o['title'] = l[0]['title']
            o['rating'] = l[0]['rating']
            o['genres'] = l[0]['genres']
            o['link'] = l[0]['alt']
        return o


class TuringRobot(Resource):
    BaseTuringUrl = "http://www.tuling123.com/openapi/api?key=6aa66264020cc0a7fb9d08646b516f9b&info="

    def get(self, keyword):
        content_url = self.BaseTuringUrl + keyword
        return ast.literal_eval(requests.get(content_url).content)


@app.route('/weather/')
def weather():
    lat = request.args.get('lat')
    lon = request.args.get('lon')
    BaseWeatherUrl = 'http://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.placefinder where text="{LAT},{LON}" AND gflags = "R") and u="c"&format=json'
    content_url = BaseWeatherUrl.replace('{LAT}', lat).replace('{LON}', lon)
    return jsonify(requests.get(content_url).json())


class Translate(Resource):
    BaseTranslateUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=edi-voice&key=417371471&type=data&doctype=json&only=translate&version=1.1&q="

    def get(self, keyword):
        content_url = self.BaseTranslateUrl + keyword
        return ast.literal_eval(requests.get(content_url).content.decode('utf-8'))


class Image(Resource):
    BaseImageUrl = "http://image.baidu.com/search/index?tn=baiduimage&word="

    def get(self, keyword):
        # content_url = self.BaseImageUrl.replace("{{ word }}", keyword)
        content_url = self.BaseImageUrl + keyword

        sougou_content = requests.get(
            "http://pic.sogou.com/pics?query=" + keyword).content
        content = requests.get(
            "http://image.haosou.com/i?q=" + keyword + "&src=srp").content
        sougou_l = re.findall(
            'http://img02.sogoucdn.com/[\/|\w|-]+.jpg', sougou_content)
        l = re.findall(
            'http:\\\\\/\\\\\/p0.so.qhimg.com\\\\[_|\w|\/|\\\\]+\.jpg', content)
        r = []
        for i in l:
            print(l.index(i))
            if l.index(i) % 4 == 0:
                r.append(i.replace("\\", ""))

        nr = []

        iii = 0
        for i in r:

            if iii % 2 == 0:
                nr.append(i)
            iii = iii + 1
        #
        d = {}
        # result_list = []
        # for i in l:
        #    result_list.append(i[0])
        nr.extend(sougou_l)

        d["link"] = content_url
        d["images"] = nr

        return d


api.add_resource(Translate, '/translate/<string:keyword>')
api.add_resource(Wiki, '/wiki/<string:keyword>')
api.add_resource(BaiduSearch, '/search/<string:keyword>')
api.add_resource(TuringRobot, '/robot/<string:keyword>')
api.add_resource(BookSearch, '/book/<string:keyword>')
api.add_resource(MovieSearch, '/movie/<string:keyword>')
api.add_resource(MusicSearch, '/music/<string:keyword>')
api.add_resource(Baike, '/baike/<string:keyword>')
api.add_resource(News, '/news/<string:keyword>')
api.add_resource(Image, '/image/<string:keyword>')
