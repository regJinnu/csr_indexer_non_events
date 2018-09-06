@StopWordFeature @TestSuiteID=9566460
Feature: Stopword related API's

  @Regression @SaveStopword
  Scenario: User wants to save new stopword
    Given [search-service] prepare save stopword using properties using properties data
    When  [search-service] send save stopword request
    Then [search-service] save stopword request response success should be 'true'

  @Regression @FindStopwordByWord
  Scenario: User wants to find stopword by word
    Given [search-service] prepare find stopword by word using properties using properties data
    When  [search-service] send find stopword by word request
    Then [search-service] find stopword request response success should be 'true'

  @Regression @FindStopwordByWrongWord
  Scenario: User wants to find stopword by wrong word
    Given [search-service] prepare find stopword by wrong word using properties using properties data
    When  [search-service] send find stopword by wrong word request
    Then [search-service] find stopword by giving wrong wrong request response success should be 'true'

  @Regression @FindStopwordByID
  Scenario: User wants to find stopword by ID
    Given [search-service] prepare find stopword by ID using properties using properties data
    When  [search-service] send find stopword by ID request
    Then [search-service] find stopword by ID request response success should be 'true'

  @Regression @FindStopwordByWrongID
  Scenario: User wants to find stopword by wrong ID
    Given [search-service] prepare find stopword by wrong  ID using properties using properties data
    When  [search-service] send find stopword by wrong ID request
    Then [search-service] find stopword by wrong ID request response success should be 'true'

  @Regression @StopwordList
  Scenario: User wants to list stopword by ID
    Given [search-service] prepare listing stopword using properties using properties data
    When  [search-service] send list stopword  request
    Then [search-service] find listing stopword request response success should be 'true'

  @Regression @UpdateStopword
  Scenario: User wants to update stopword by ID
    Given [search-service] prepare update stopword using properties using properties data
    When  [search-service] send update stopword by ID request
    Then [search-service] update stopword by ID request response success should be 'true'

  @Regression @UpdateWithWrongID
  Scenario:User wants to update stopword by wrong ID
    Given [search-service] prepare update stopword by wrong ID using properties using properties data
    When  [search-service] send update stopword by wrong ID request
    Then [search-service] update stopword by wrong ID request response success should be 'false'

  @Regression @DeleteStopWord
  Scenario: User wants to delete stopword
    Given [search-service] prepare delete stopword using properties using properties data
    When  [search-service] send delete stopword by ID request
    Then [search-service] delete stopword by ID request response success should be 'true'


