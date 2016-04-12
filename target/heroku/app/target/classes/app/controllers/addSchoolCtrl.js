angular.module('addSchoolCtrl', [])

.controller('addSchoolController', function($scope, $http){

   var school = this;

   school.addSchool = function(){
    
      $http.post('/school/add', school.schoolData)
      
      .then(function(response){
      	  school.schoolData = '';
          $scope.success = response.data.success;
          $scope.message = response.data.message;

      });

   };

  
});