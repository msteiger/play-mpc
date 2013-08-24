$(function() 
{
	$("#slider-range-min").slider(
	{
		range : "min",
		value : 40,
		min : 0,
		step : 5,
		max : 100,
		slide : function(event, ui) 
		{
			$('#volume').html(ui.value);

			jsRoutes.controllers.Application.volume(ui.value).ajax(
			{
				error : function(data) 
				{
					alert("AJAX call to volume(int amount) failed ");
				}
			});
		}
	});

	$("#volume").html($("#slider-range-min").slider("value"));
	
	// for some reason this has to be inside the magical $(function()) scope
	
	// Add onClick handler for all playlist table rows
	$('#playlist-table').on('click', 'tbody tr', function(event) 
	{
		$(this).addClass('highlight').siblings().removeClass('highlight');
		
		// read HTML5 data attributes from <tr data-pos="">
		var pos = $(this).data("pos");
		
		jsRoutes.controllers.Application.selectSong(pos).ajax(
		{
			error : function(data) 
			{
				alert("AJAX call failed ");
			}
		});
	});

});

