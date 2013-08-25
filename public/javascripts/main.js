$(function() 
{	
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

