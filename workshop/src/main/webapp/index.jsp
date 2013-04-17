
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>ReFL Workshop</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
		 body {
		   padding-top: 60px;
		   padding-bottom: 40px;
		 }
		 .sidebar-nav {
		   padding: 9px 0;
		 }
		
		 @media (max-width: 980px) {
		   /* Enable use of floated navbar text */
		   .navbar-text.pull-right {
		     float: none;
		     padding-left: 5px;
		     padding-right: 5px;
		   }
		 }

        /* Styles for JSON representation of form data */
		.string { color: green; }
		.number { color: darkorange; }
		.boolean { color: blue; }
		.null { color: magenta; }
		.key { color: red; }

    </style>
    <link href="css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
	<link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
	<link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">
	<link rel="shortcut icon" href="../assets/ico/favicon.png">
	
</head>

<body>

	<!-- Error Window -->
	<div id="errorWindow" class="modal hide fade">
	  <div class="modal-header">
	    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	    <h3>Error</h3>
	  </div>
	  <div class="modal-body">
	    Body here
	  </div>
	  <div class="modal-footer">
	    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</a>
	  </div>
	</div>
	<!-- END OF Error Window -->
	
	<!-- Error Window -->
	<div id="errorsCompilationWindow" class="modal hide fade">
	  <div class="modal-header">
	    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	    <h3>Errors detected during compilation</h3>
	  </div>
	  <div class="modal-body">
	    Body here
	  </div>
	  <div class="modal-footer">
	    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</a>
	  </div>
	</div>
	<!-- END OF Error Window -->


	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
	        <div class="container-fluid">
	          	<button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
	            	<span class="icon-bar"></span>
	            	<span class="icon-bar"></span>
	            	<span class="icon-bar"></span>
	          	</button>
	          	<a class="brand" href="#">ReFL Workshop</a>
	          	<div class="nav-collapse collapse">
	            	<ul class="nav">
	              		<li class="active"><a href="#main">Main</a></li>
	                    <li><a href="#about">About</a></li>
	                </ul>
	            </div><!--/.nav-collapse -->
	        </div>
		</div>
	</div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span3">
          <div class="well sidebar-nav">
    		<div class="row" style="padding-left: 15px; padding-right: 15px;">
    			<a class="pull-right" id="refreshForms" href="#"><i style="opacity: 50" class="icon-refresh"></i></a>
    			<a class="pull-right" style="margin-right: 5px" id="newForm" href="#"><i style="opacity: 50" class="icon-plus"></i></a>
    		</div>
            <ul id="formsNavList" class="nav nav-list">
            
            </ul>
          </div><!--/.well -->
        </div><!--/span-->
        <div class="span9">
          	<div id="alert-area" class="alert hide">
  				<button type="button" class="close" data-dismiss="alert">&times;</button>
			</div>

          	<div id="home">
				<div class="hero-unit">
				  <h1>ReFL Workshop</h1>
				  <p>A tool for creating  and testing eForms using the ReFL language</p>
				  <p>Please use the menu on the left to create, edit, and fill in eForms</p>
				</div>
          	</div>
          	
          	<div id="formDataBrowser" class="hidden"><!-- Form data entry pane -->
          		<div class="well well-small">
					<button id="newFormInstance" class="btn">New</button>
 				</div>
          		<div>
          		  Table here
          		</div>
          	</div>
          	<div id="formDataEntryPane" class="hidden"><!-- Form data entry pane -->
          		
          	</div>
			<div id="formEditor" class="hidden"><!-- Form editor -->
				<ul class="nav nav-tabs" id="formEditorTabs">
					<li class="active"><a href="#formEditorTab" data-toggle="tab">Editor</a></li>
					<li><a href="#formPreviewTab" data-toggle="tab">Preview</a></li>
				</ul>
				 
				<div class="tab-content">
					<div class="tab-pane active" id="formEditorTab">
						<form id="eform">
							<fieldset>
								<input id="formId" name="formId" type="hidden">
								<input id="formVersion"  name="formVersion" type="hidden">
								<textarea id="formDef" name="formDef" rows="20" class="span12" style="font-family: monospace; font-size: 12pt"></textarea>
							</fieldset>
						</form>
						<div class="form-actions">
							<button id="saveForm" class="btn btn-primary">Save</button>
							<button id="cancelForm" class="btn">Cancel</button>
						</div>
					</div>
					<div class="tab-pane" id="formPreviewTab">
						<div id="#formPreviewTools" class="well well-small">
							<button id="viewJson" class="btn">View JSON</button>
							<!-- View JSON Window -->
							<div id="viewJsonWindow" class="modal hide fade">
							  <div class="modal-header">
							    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							    <h3>Form Data as JSON</h3>
							  </div>
							  <div class="modal-body" style="font-family: monospace; font-size: 12pt">
							    <pre style="background-color: white; border: none"></pre>
							  </div>
							  <div class="modal-footer">
							    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</a>
							  </div>
							</div>
							<!-- END OF View JSON Window -->
						</div>
						<div id="formPreviewArea"></div>
					</div><!--/tab-pane-->
				</div><!--/tab-content-->						
			</div><!--/formEditor-->
		</div><!--/span-->
	</div><!--/row-->

	<hr>

	<footer>
		<p>&copy; Malcolm Spiteri 2013</p>
	</footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.js"></script>
	<script src="js/efrm.js"></script>
	<script src="js/efrmrt.js"></script>

</body>
</html>
