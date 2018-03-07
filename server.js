/**
 * Indra Backend
 * 
 * Mohammed Sohail <sohailsameja@gmail.com>
 * Released under the AGPL-3.0 License
 **/


// node modules
const bodyParser = require("body-parser");
const util = require("util");
const express = require("express");


// own modules
const lib = require("./lib/parallelExec");


// module variables
const app = express();
const serverPort = process.argv[2];


// server core
app.disable("x-powered-by");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


app.get("/api", (req, res) => {
    const textString = req.query.text;

    lib.execParallel(textString);
    res.status(200).json({ message: "Processing request" });
});

app.listen(serverPort, () => {
    console.log(`Server started on port ${serverPort}`);
});