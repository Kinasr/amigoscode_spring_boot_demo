## Test Cases

### Get Students
| Summary | Expected Result | Expected Status Code | Is Done |
| -------------- | ------------- | --------------- | ------- |
| Get Students while there are some in the database | The Students in the database should return | Ok | ✔
| Get Students while there is no records in the database | The data will be empty | Ok | ✔


### Create Student

| Summary | Error message | Expected Status Code | Is Done |
| ------------- | ------------- | --------------- | --- |
| Create a new Student with valid data |  | Created | ✔
| Create a new Student with name length 29 |  | Created |
| Create a new Student with age is 18 exact |  | Created |
| Create a new Student with Null name | Student name can't be null | Bad Request | ✔
| Create a new Student with a name greater than thirty | Student Name should be less than 30 | Bad Request | ✔
| Create a new Student with a name not start with capital letter "ali" | Student Name should start with capital letter and contains letters only | Bad Request | ✔
| Create a new Student with a name that Contains a special character in the middle "A!i" | Student Name should start with capital letter and contains letters only | Bad Request | ✔
