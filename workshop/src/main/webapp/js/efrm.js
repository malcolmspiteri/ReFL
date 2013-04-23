$(document).ready(
		function() {
			console.log("ready!");

			reloadForms(function() {
				bindEditFormEvents();
			});
			
			$("#newForm").click(function() {
				$("#eform #formId").val(0);
				$("#eform #formVersion").val(0);
				$("#eform #formDef").val("");
				setToFormEditMode();
			});

			$("#cancelForm").click(function() {
				setToFormBrowseMode();
			});

			$("#refreshForms").click(function() {
				reloadForms(function() {
					bindEditFormEvents();
				});
			});

			function bindEditFormEvents() {
				$("a[data-form-id]").off("click", editForm);
				$("a[data-form-id]").on("click", editForm);
				
				$("#formsNavList a[data-form-id]").off("click", highlightForm);
				$("#formsNavList a[data-form-id]").on("click", highlightForm);
				
			}

			function highlightForm(e) {
				$("#formsNavList li.active").removeClass("active");
				$(this).parent().addClass("active");				
			}
			
			function editForm(e) {
				var id = $(this).attr("data-form-id");
				console.log("Editing form:" + id);
				$.getJSON('services/forms/' + id, function(data) {
					$("#eform #formId").val(data.id);
					$("#eform #formVersion").val(data.ver);
					$("#eform #formDef").val(data.def);
					setToFormEditMode();
				});

			}

			function reloadForms(ready) {
				$.getJSON('services/forms', function(data) {
					var items = [];

					for ( var i = 0, len = data.length; i < len; i++) {
						var frm = data[i];
						console.log(frm);
						items.push(formToLi(frm));
					}

					$("#formsNavList").html("<li class='nav-header'>Forms</li>" +
							items.join(''));

					ready();

				});
			}

			function formToLi(frm) {
				  return '<li>' + 
				  '<a href="#" data-toggle="tooltip" title="Version ' + frm.ver + '. Last modified on ' + frm.created + '" ' +
				  'data-form-id="' + frm.id + '"><span class="icon-black icon-file"/>' + frm.label + '</a>' +
				  '</div>' + 
				  '</li>';
			}

			$("a[href='#formPreviewTab']").on('show', function(e) {
				console.log("Preview tab activated");
				$("#formPreviewTab #formPreviewArea").empty();
				$.ajax("services/forms/previewer", {
					type : "POST",
					cache : false,
					contentType : "text/plain",
					data : $("#eform #formDef").val(),
					success : function(data) {
						$("#formPreviewTab #formPreviewTools").removeClass("hidden");
						$("#formPreviewTab #formPreviewArea").html(data);
					},
					error : function(jqXHR, textStatus, errorThrown) {
						e.preventDefault();
						showError(jqXHR, textStatus, errorThrown);
					}
				});
			});

			$("#saveForm").click(
					function() {
						console.log($("#eform").serialize());
						var id = $("#eform #formId").val();
						if (id == 0) {
							// new
							$.ajax("services/forms", {
								type : "POST",
								cache : false,
								data : $("#eform").serialize(),
								success : function(data) {
									addFormToGrid(data),
									setToFormBrowseMode();
									alertSuccess("Form was saved successfully");
								},
								error: showError
							});
						} else {
							// update
							$.ajax("services/forms/" + id, {
								type : "PUT",
								cache : false,
								data : $("#eform").serialize(),
								success : function(data) {
									updateGrid(data),
									setToFormBrowseMode();
									alertSuccess("Form was updated successfully");
								},
								error: showError
							});
						}
						

						function addFormToGrid(data) {
							$(formToLi(data)).appendTo("#formsTable tbody");
							bindEditFormEvents();
						}

						function updateGrid(data) {
							$("button[data-form-id='" + id + "'] ")
									.parentsUntil("tbody").replaceWith(
											$(formToLi(data)));
							bindEditFormEvents();
						}

					});
			
			function showError(jqXHR, textStatus, errorThrown) {
				if (jqXHR.status == 422) {
					$("#errorsCompilationWindow div.modal-body").html(jqXHR.responseText);
					$('#errorsCompilationWindow	').modal();					
				} else {
					if (jqXHR.responseText != "") {
						$("#errorWindow div.modal-body").html(jqXHR.responseText);
					} else {
						$("#errorWindow div.modal-body").html("An undefined error has occured. Unofortunately, no further information about the nature of the error could be acquired.");
					}
					$('#errorWindow').modal();					
				}
				errorsCompilationWindow
			}

			function setToFormBrowseMode() {
				hideFormEditor();
				showHome();
			}

			function setToFormEditMode() {
				showFormEditor();
				hideHome();
			}

			function showHome() {
				$("div#home").removeClass("hidden");
			}

			function hideHome() {
				$("div#home").addClass("hidden");
			}

			function showFormEditor() {
				$("#formEditor").removeClass("hidden");
			}

			function hideFormEditor() {
				$("#formEditor").addClass("hidden");
			}
			
			$("#viewJson").click(function () {
				$("#viewJsonWindow div.modal-body pre:first").html(syntaxHighlight(frm.val()));
				$('#viewJsonWindow').modal();
			});
			
			function syntaxHighlight(json) {
			    if (typeof json != 'string') {
			         json = JSON.stringify(json, undefined, 4);
			    }
			    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
			    json =  json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
			        var cls = 'number';
			        if (/^"/.test(match)) {
			            if (/:$/.test(match)) {
			                cls = 'key';
			            } else {
			                cls = 'string';
			            }
			        } else if (/true|false/.test(match)) {
			            cls = 'boolean';
			        } else if (/null/.test(match)) {
			            cls = 'null';
			        }
			        return '<span class="' + cls + '">' + match + '</span>';
			    });
			    return json;
			}
			
			function alertSuccess(message) {
				$("div#alert-area")
					.addClass("alert-success")
					.append(message);
				
				$("div#alert-area").fadeIn(800, function() {
					$(this).delay(800).slideUp(1000);					
				});
			}

		});