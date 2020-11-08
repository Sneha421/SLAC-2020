var app = angular.module('myApp1', []);
app.controller('queueCtrl', function($scope, $http)
{
	$http.get("http://localhost:8080/getQueue")
		.then(function (response)
		{
			$scope.queue = response.data
			console.log(response.data);
		})
	
	$scope.submit = function()
	{
		var name = document.getElementById("name").value;
		var number = document.getElementById("cntct").value;
		var token = document.getElementById("qNo").value;
		console.log(name)
		$http.put("http://localhost:8080/postQueue?patientName="+name+"&phoneNo="+number+"&tokenNo="+token)
			.then(function (response) {
			
			})
	}
	sendSMS = function (element)
	{
		var formID = element.parentNode.parentNode.rowIndex
		
		
		var tokenNo = document.getElementById("queueTable").rows[formID].cells[2].innerHTML;
		
	/*	console.log( document.getElementById("queueTable").rows[1].cells[tokenNo].innerHTML)
		
		console.log( document.getElementById("queueTable").rows[2].cells[tokenNo].innerHTML)*/
		
		
		var rowCount = document.getElementById("queueTable").rows.length;
		
		
		
		for (var i = tokenNo+2; i < tokenNo+5; i++)
		{
			
			var iterator = i%10
			
			console.log(document.getElementById("queueTable").rows[iterator].cells[tokenNo].innerHTML)
			
			var positionNo = iterator-tokenNo;
			var phoneNo = document.getElementById("queueTable").rows[iterator].cells[tokenNo].innerHTML;
			
			$http.put("http://localhost:8080/sendSMS?phoneNumber="+phoneNo+"&positionNo="+positionNo)
				.then(function (response)
				{
					console.log(response);
				})
		}
	}
	
	
});