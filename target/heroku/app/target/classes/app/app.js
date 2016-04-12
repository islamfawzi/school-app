var app = angular.module('MyApp', ['appRoutes']);


app.controller('SchoolController', function($scope, $http){
    
	var school = this;

    $scope.schools_length = 0;  
    $http.get("/schools")
       .then(function (response) 
       {
       	$scope.schools = response.data;
        $scope.schools_length = response.data.length;
       });

    
   
    school.deleteSchool = function(school_id, schools_length)
    {
       console.log(school_id);
       $http.delete("school/" + school_id);
     
       $scope.schools_length = schools_length - 1; 
          
    }   
})

.controller('addSchoolController', function($scope, $http, $routeParams, $timeout, $location){

   var school = this;
   school.contacts = [1];

   if($routeParams.id){
   	   $http.get("/school/" + $routeParams.id)
		         .then(function (response) 
			         {
		         school.schoolData = response.data[0];
		         school.contacts   = response.data[0].contacts;
                 school.schoolData.contacts = 
                 {
                    ids:   [],
                    type:  [],
                    value: []
                 };
                 

                 for(var x in school.contacts){
                    school.schoolData.contacts.ids[x] = school.contacts[x].id; 
                    school.schoolData.contacts.type[x] = school.contacts[x].type; 
                    school.schoolData.contacts.value[x] = school.contacts[x].value; 
                 }
                 
                 if(school.contacts.length == 0){
                	 school.contacts = [1];
                 }
                 school.update = true;

			         });	
   }
   

   school.addSchool = function(){
	   
      if(typeof school.schoolData.id !== "undefined")
      {
    	  var conts = school.schoolData.contacts;
    	  school.schoolData.contacts  = [];
    	  
    	  if(typeof conts != "undefined"){
	    	  // convert objects to arrays
	    	  var types = Object.keys(conts.type).map(function (key) {return conts.type[key]}); 
	    	  var values = Object.keys(conts.value).map(function (key) {return conts.value[key]}); 
	    	  var ids = Object.keys(conts.ids).map(function (key) {return conts.ids[key]}); 
	    	  
	    	  types.forEach(function(item, index){
	    		 var contact = {"type": item, "value": values[index], "id": ids[index]};
	    		 school.schoolData.contacts.push(contact);
	    	  });
    	  }
       
        $http.put('/school/' + school.schoolData.id, school.schoolData)
	        .then(function(response){
	      	  $scope.success = response.data.success;
	      	  $scope.message = response.data.message;

	          if($scope.success){
	          	$timeout(function(){
	          		$location.path("/");
	          	}, 3000);
	          }
	        });

      }
      else
      {
    	  var conts = school.schoolData.contacts;
    	  school.schoolData.contacts  = [];
    	  
    	  if(typeof conts != "undefined"){
    		  // convert objects to arrays
        	  var types = Object.keys(conts.type).map(function (key) {return conts.type[key]}); 
        	  var values = Object.keys(conts.value).map(function (key) {return conts.value[key]}); 
        	  
        	  types.forEach(function(item, index){
        		 var contact = {"type": item, "value": values[index]};
        		 school.schoolData.contacts.push(contact);
        	  });
    	  }
    	  
    	 // POST to REST 
	    $http.post('/school', school.schoolData)
	        .then(function(response){
	      	  school.schoolData = '';
              school.contacts = [1];
	          $scope.success = response.data.success;
	          $scope.message = response.data.message;
	        });

      }
     
   }

   school.addmore = function()
   {
     
     school.contacts.push(school.contacts.length + 1);
   }
});