package by.yazazzello.forum.client.features.thread

open class WrapperHTML(private var htmlString: String?) {
//    fun formattedHTML(): String = "<head><meta name='viewport' content='initial-scale=1.0'/></head><body>$htmlString</body>"

    fun formattedWebviewFallback(): String {
        return """
       <html>
        <head>
        <meta name='viewport' content='user-scalable=no'/>
<script type="text/javascript"></script>
            <style type="text/css">
/* -- mgs thread --*/
.b-messages-thread {
    padding-bottom:10px; padding-top:1px;
    }
    .msgpost {
        position:relative; background-color:#e1e5e8; border:1px solid #e1e5e8; padding-bottom:10px; margin-bottom:1px; zoom:1;
        }
    .msgfirst {
        border-color:#ccc; background-color:#fff; margin-bottom:10px;
        }
        .msgfirst .mtauthor-nickname .sh {
            background-position:0 -600px;
            }
    .msgpost-odd {
        background-color:#f0f1f2; border-color:#f0f1f2;
        }
    .msgpost:hover {
        z-index:1015;
        }
        .msgpost:after {
            content: "\0020"; display: block; height: 0; visibility: hidden; clear:both;
            }
    /* -- / mgs thread --*/

    /* -- spoiler --*/
        .msgpost-spoiler {
            margin:0 0 16px 0; color:#333; font-size:13px; line-height:18px; -webkit-font-smoothing: subpixel-antialiased; zoom:1;
            }
        .msgpost-spoiler:after {
            content: "\0020"; display: block; height: 0; visibility: hidden; clear:both;
            }
        .msgpost-spoiler-outer {
            float:left; background-color:#f9fafa; border:1px solid #d4d4d4; padding:5px 12px 6px; max-width:90%;
            }
        .msgpost-spoiler-i {
            background:url(../img/i-spoiler.png) no-repeat 0 2px; padding:0 0 0 22px;
            }
            .msgpost-odd .msgpost-spoiler-outer {
                background-color:#fbfbfb;
                }
            .msgpost-spoiler-i p {
                padding-bottom:0;
                }
        a.msgpost-spoiler-hd {
            font:13px/18px Arial,Helvetica; text-decoration:none;
            }
            a.msgpost-spoiler-hd:hover {
                text-decoration:none; border-bottom:1px dotted;
                }
        .msgpost-spoiler-txt {
            display:block;
            }
    .msgpost-spoiler-open {

        }

    .msgpost-spoiler-open .msgpost-spoiler-txt {
            display:block; clear:both; padding-top:3px; padding-bottom:2px;
            }
    .uncited .msgpost-spoiler-outer {
            max-width:100%;
            }
    /* -- / spoiler --*/


     p.msgpost-img__p {
        padding-bottom:17px;
        }
    .msgpost-img__span {
        position: relative;
        display: block;
        width: 100%;
        overflow: hidden;
        }
    .msgpost-img {
        vertical-align: top;
        border: 1px solid #d4d4d4;
        max-width: 100%;
        }

        .uncited {
    background-color:#f9fafa; border:1px solid #d4d4d4; padding:10px 12px 8px; color:#000; font-size:13px; line-height:15px; margin-bottom:10px; -webkit-font-smoothing: subpixel-antialiased;
    -webkit-margin-start: 0px;
    -webkit-margin-end: 0px;
    }
    .msgpost-odd .uncited {
        background-color:#fbfbfb;
        }
    .uncited .uncited {
        width:auto; margin-top:13px; margin-bottom:0;
        }
    .uncited p {
        padding-bottom:0; margin-top:13px;
        }
    .uncited cite {display:block; color:#000; font:bold 13px/18px Arial,Helvetica; margin-top:7px; margin-bottom:0;}
    .uncited > cite:first-child {
        margin-top:0;
        }
    .uncited > p:first-child {
        padding-bottom:0; margin-top:0;
        }
    .uncited > .uncited:first-child {
        margin-top:4px; margin-bottom:0;
        }


    .codebox dd {
    display:block; background-color:#f9fafa; border:1px solid #d4d4d4; padding:10px 12px 8px; color:#333; font:13px/18px 'Courier','Courier New'; margin-bottom:17px; -webkit-font-smoothing: subpixel-antialiased;
    }
    .msgpost-odd .codebox dd {
        background-color:#fbfbfb;
        }
    .codebox dt {
        color:#333; font:11px/14px Verdana,Tahoma; padding-bottom:7px; display:none;
        }
    .codebox dt a {
        text-decoration:none; border-bottom:1px dotted;
        }

        body {
            font-size: 14px;
            text-align: justify;
        }

        </style>
        </head>
        <body>
        $htmlString
        </body>
        </html>
    """.replace("\\r", "").replace("\\n", "") //clean special characters only for webview, because htmltextview handles it buy itself
    }
}

