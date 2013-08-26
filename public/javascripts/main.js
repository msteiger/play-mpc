$(function() 
{	
	$("#volume-slider").slider(
	{
		range : "min",
		value : $("#volume").text(),
		min : 0,
		step : 5,
		max : 100,
		slide : function(event, ui) 
		{
			$("#volume").html(ui.value);
			jsRoutes.controllers.Application.setVolume(ui.value).ajax({});
		}
	});

    // Add onClick handler for all playlist table rows
	$('#playlist').on('click', 'tbody tr', function(event) 
	{
		$(this).addClass('highlight').siblings().removeClass('highlight');
		
		// read HTML5 data attributes from <tr data-pos="">
		var pos = $(this).data("pos");
		
		jsRoutes.controllers.Application.selectSong(pos).ajax({});
	});

});

