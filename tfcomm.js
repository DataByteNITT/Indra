/***/


// npm-installed modules
const bodyParser = require("body-parser");
const express = require("express");
const PythonShell = require("python-shell");


// module variables
const app = express();
const pyshell = new PythonShell("predict.py");

const serverPort = 3050;
const resObj = {};


app.disable("x-powered-by");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

pyshell.on("message", (msg) => {
    const formattedRes = msg.replace("\r", "");
    resObj.result = formattedRes;
});


app.get("/nn", (req, res) => {
    const textString = req.query.text;
    
    pyshell.send(textString);
    resObj.text_string = textString;
    resObj.message = "Successfully sent request";
    
    setTimeout(() => {
        res.status(200).json(resObj);
    }, 2500);
});


app.listen(serverPort, () => {
    console.log(`Server started on port ${serverPort}`);
});