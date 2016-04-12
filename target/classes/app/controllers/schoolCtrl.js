angular.module('schoolCtrl', [])

.controller('SchoolController', function($scope, $http){
	
	var school = this;

    $http.get("/school/list")
       .then(function (response) 
       {
       	$scope.schools = response.data;
       });

    school.getSchool = function(school_id)
    {
    	$scope.add_edit = true;
    	$http.get("/school/get/"+school_id)
	         .then(function (response) 
		         {
		       	   $scope.school = response.data;
		         });
    }

    school.deleteSchool = function(school_id)
    {
       $http.delete("school/delete/"+school_id)
            .then(function(response)
            	{
            		console.log(response.data);
            		$scope.school_id = false;
            	});

    }   
});