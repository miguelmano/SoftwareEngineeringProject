describe('Check 2019 teacher dashboard data', () => {
  let date;

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    cy.demoTeacherLogin();
    cy.log('student submits a question');
    
    cy.addQuizz(1, 'Quizz 1 - ${academicYear}', 'TEST- ${academicYear}', date.toUTCString());

    cy.addQuestionToQuizz('Question 0', 'Quizz 1 - Year 2019/2020', 1);
    cy.addQuestionToQuizz('Question 1', 'Quizz 1 - Year 2019/2020', 2);
    cy.addQuestionToQuizz('Question 2', 'Quizz 2 - Year 2019/2020', 1);
    cy.addQuestionToQuizz('Question 3', 'Quizz 2 - Year 2019/2020', 2);
    cy.addQuestionToQuizz('Question 4', 'Quizz 3 - Year 2019/2020', 1);
    cy.addQuestionToQuizz('Question 5', 'Quizz 3 - Year 2019/2020', 2);
    cy.logout();


    afterEach(() => {
      cy.contains('Logout').click();
    });

    it('teacher accesses dashboard', () => {
      cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
        'getDashboard'
      );
      cy.intercept('PUT', '**/teachers/dashboards/*/totalStudents').as(
        'totalStudents'
      );
      cy.intercept('PUT', '**/teachers/dashboards/*/numStudentsWhoSolved75MoreQuestions').as(
        'numStudentsWhoSolved75MoreQuestions'
      );
      cy.intercept('PUT', '**/teachers/numStudentsWhoSolvedAtLeast3Quizzes/*').as(
        'numStudentsWhoSolvedAtLeast3Quizzes'
      );

      cy.demoTeacherLogin();

      cy.getDemoCourseExecutionId2019().then((result) => {
        cy.request({
          method: 'GET',
          url:
            'http://localhost:8080/teachers/dashboards/executions/' +
            result[0].id,
          headers: {
            Authorization: 'Bearer ' + Cypress.env('token'),
          },
        });
      });

      cy.get('[data-cy="dashboardMenuButton"]').click();
      cy.wait('@getDashboard');

      cy.get('[data-cy="totalStudents"]')
        .eq(0)
        .click();
      cy.wait('@totalStudents');

      cy.get('[data-cy="numStudentsWhoSolved75MoreQuestions"]')
        .eq(0)
        .click();
      cy.wait('@numStudentsWhoSolved75MoreQuestions');

      cy.get('[data-cy="numStudentsWhoSolvedAtLeast3Quizzes"]')
        .eq(0)
        .click();
      cy.wait('@numStudentsWhoSolvedAtLeast3Quizzes');


      cy.get('[data-cy="totalStudents"]').should('not.exist');
      cy.get('[data-cy="numStudentsWhoSolved75MoreQuestions"]').should('not.exist');
      cy.get('[data-cy="numStudentsWhoSolvedAtLeast3Quizzes"]').should('not.exist');

    });
  });
});
  