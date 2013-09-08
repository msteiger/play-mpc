
$(function() 
{	
	$("#songpos-slider").slider(
	{
		range : "min",
		min : 0,
		max : $("#songpos-text").data("length"),
		value : $("#songpos-text").data("elapsed"),
		step : 1,
		slide : function(event, ui) 
		{
//			$("#volume").html(ui.value);
			
//			jsRoutes.controllers.Application.setVolume(ui.value).ajax({});
		}
	});

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
			updateVolumeIcon(ui.value);
			
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

	updateVolumeIcon($("#volume").text());
});


var updateVolumeIcon = function(volume)
{
	$("#volume-icon").removeClass("glyphicon-volume-off");
	$("#volume-icon").removeClass("glyphicon-volume-up");
	$("#volume-icon").removeClass("glyphicon-volume-down");
	
	if (volume == 0)
		$("#volume-icon").addClass("glyphicon-volume-off"); else
	if (volume < 50)
		$("#volume-icon").addClass("glyphicon-volume-down"); else
		$("#volume-icon").addClass("glyphicon-volume-up");			
}
