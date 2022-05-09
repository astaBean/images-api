Feature: annotation
#This is how background can be used to eliminate duplicate steps

# Testing if you can login to site with any credentials
  Scenario:
    Given I am using "chrome" browser
    Given I go to ImageGallery login page
    When I enter username as "Aistyna123456"
    When I enter password as "passwordwhatever"
    Then Login should fail
    Then I close browser

# Testing registration and logging out
  Scenario:
    Given I am using "chrome" browser
    Given I go to ImageGallery login page
    Then I go to registration page
    When I enter username as "UserName1234567"
    When I enter password with confirmation as "passwordtest"
    Then I am logged in
    Then I will log out
    Then I close browser
