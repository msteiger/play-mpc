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
});
