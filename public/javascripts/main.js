
var block_songpos_update = false;

$(function() 
{	
	$("#songpos-slider").slider(
	{
		range : "min",
		min : 0,
		max : $("#songpos-length").data("length"),
		value : $("#songpos-elapsed").data("elapsed"),
		step : 1,
		
		slide : function(event, ui) 
		{
			$("#songpos-elapsed").html(format_time(ui.value));
		},
		
		start : function(event, ui)
		{
			block_songpos_update = true;
		},

		stop : function(event, ui) 
		{
			block_songpos_update = false;
			
			$("#songpos-elapsed").html(format_time(ui.value));
			
			jsRoutes.controllers.Application.setSongPos(ui.value).ajax({});
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
	
	$('#database').on('click', '.dbentry', function(event)
	{
		var href = $(this).data("ref");
		var elem = $(this);
		
		jsRoutes.controllers.Application.addDbEntry(href).ajax(
		{
			// reload, so that the "flash" error message is displayed
			error: function(html) { window.location.reload(); },
			success: function(html) { elem.addClass('inplaylist'); elem.css("color", "lightgray"); }
		});
	});

    // Add onClick handler for all playlist table rows
	$('#playlist').on('click', 'tbody tr', function(event) 
	{
		$(this).addClass('highlight').siblings().removeClass('highlight');
		
		// read HTML5 data attributes from <tr data-pos="">
		var pos = $(this).data("pos");
		
		jsRoutes.controllers.Application.selectSong(pos).ajax({});
	});
	
	$('#playlist').on('click', '.remove', function(event)
	{
		// don't fire the onClick() event for the parent
		event.stopPropagation();

		var pos = $(this).data("pos");
		
		jsRoutes.controllers.Application.remove(pos).ajax(
		{
			// don't update page content - just reload
			complete: function(html) { window.location.reload(); }
		});
	});
	
	$('#prevsong').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.prevSong().ajax({});
	});
	
	$('#playsong').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.playSong().ajax({});
	});
	
	$('#nextsong').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.nextSong().ajax({});
	});
	
	$('#stopsong').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.stopSong().ajax({});
	});
	
	$('#shuffle').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.toggleShuffle().ajax({});
	});
	
	$('#repeat').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.toggleRepeat().ajax({});
	});
	
	$('#single').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.toggleSingleMode().ajax({});
	});
	
	$('#consume').on('click', function(event)
	{
		event.preventDefault();
		jsRoutes.controllers.Application.toggleConsuming().ajax({});
	});

	updateVolumeIcon($("#volume").text());
});


// this function also exists as scala variant in playlist.scala.html
function format_time(secs)
{
	var min = Math.floor(secs / 60);
	var sec = secs % 60;
	
	if (sec < 10)
		sec = "0" + sec;
	
	return min + ":" + sec;
}


var setBtnActive = function(btn, value)
{
	if (value == 1)
	{
		btn.addClass("btn-primary");
		btn.removeClass("btn-default");
	}
	else
	{
		btn.addClass("btn-default");
		btn.removeClass("btn-primary");			    	
	}
}

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

function showAlert(type, text)
{
	var box = $("#" + type + "-box"); 
	box.removeClass("hidden");
	
	var span = $("#" + type + "-text"); 
	$(span).html(text);
}
