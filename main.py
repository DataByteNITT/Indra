from googleSearcher import searcher
from flask import Flask
from flask import jsonify
from flask import request
import json

app = Flask(__name__)


@app.route('/hello', methods=['POST'])
def hello():
    catch_string = request.get_json(cache=True)
    print(catch_string['payload'])
    g = searcher.GoogleSearcher("hello jim!", 0, 1, 2)
    j = json.dumps(catch_string['payload']['entities_analysis'])
    g.process(j)
    return jsonify(result={"ok": "true"})


if __name__ == '__main__':
    app.run()
