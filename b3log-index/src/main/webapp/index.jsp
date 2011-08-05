<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Home</title>
        <meta charset="utf-8">
        <link rel="stylesheet" href="css/reset.css" type="text/css" media="all">
        <link rel="stylesheet" href="css/layout.css" type="text/css" media="all">
        <link rel="stylesheet" href="css/style.css" type="text/css" media="all">
        <script type="text/javascript" src="js/jquery-1.5.2.js" ></script>
        <script type="text/javascript" src="js/cufon-yui.js"></script>
        <script type="text/javascript" src="js/cufon-replace.js"></script> 
        <script type="text/javascript" src="js/Terminal_Dosis_300.font.js"></script>
        <script type="text/javascript" src="js/atooltip.jquery.js"></script>
        <script src="js/roundabout.js" type="text/javascript"></script>
        <script src="js/roundabout_shapes.js" type="text/javascript"></script>
        <script src="js/jquery.easing.1.2.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/script.js"></script>
        <!--[if lt IE 9]>
	<script type="text/javascript" src="js/html5.js"></script>
	<style type="text/css">
		.bg {behavior:url(js/PIE.htc)}
	</style>
        <![endif]-->
        <!--[if lt IE 7]>
	<div style='clear:both;text-align:center;position:relative'>
		<a href="http://www.microsoft.com/windows/internet-explorer/default.aspx?ocid=ie6_countdown_bannercode"><img src="http://www.theie6countdown.com/images/upgrade.jpg" border="0" alt="" /></a>
	</div>
        <![endif]-->
    </head>
    <body id="page1">
        <div class="body1">
            <div class="body2">
                <div class="body3">
                    <div class="main">
                        <!-- header -->
                        <header>
                            <%@ include file="common/wrapper.lamb"%>	
                            <%@ include file="common/relative.lamb"%>
                        </header>	
                        <!-- / header-->
                        <%@ include file="common/content.lamb"%>
                    </div>
                </div>
            </div>
        </div>
        <div class="body4">
            <div class="main">
                <%@ include file="common/content2.lamb"%>
            </div>
        </div>
        <%@ include file="common/footer.lamb"%>

        <script type="text/javascript"> Cufon.now(); </script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#myRoundabout').roundabout({
                    shape: 'square',
                    minScale: 0.93, // tiny!
                    maxScale: 1, // tiny!
                    easing:'easeOutExpo',
                    clickToFocus:'true',
                    focusBearing:'0',
                    duration:800,
                    reflect:true
                });
            });
        </script>
    </body>
</html>
