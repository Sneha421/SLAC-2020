var app = angular.module('myWebApp',[])

app.controller('Ctrl1',function ($scope,$http)
{
/*	$scope.sub= function ()
	{
		console.log(count)
		console.log($scope.CompanyCode)
		$http.post("api/CompanyCode/Post",$scope.CompanyCode)
			.then(function (response) {
			
			});
	}*/
	
	$scope.getData = function ()
	{
		$http.get("http://localhost:8080/getAllEmp")
			.then(function (response)
			{
				console.log(response)
			})
	}

})