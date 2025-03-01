package pt.ulisboa.tecnico.socialsoftware.tutor.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DemoService {
    List<Integer> questions2Keep = Arrays.asList(1320, 1940, 1544, 11081, 11082);

    List<Integer> questionsInQuizzes = Arrays.asList(1940, 11081, 11082);

    Integer quiz2Keep = 40438;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Autowired
    private AuthUserService authUserService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDashboards() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser() != null && user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .flatMap(student -> student.getDashboards().stream())
                .forEach(dashboard -> {
                    dashboard.remove();
                    this.studentDashboardRepository.delete(dashboard);
                });

        Set<DifficultQuestion> difficultQuestionsToRemove = courseExecutionRepository.findById(courseExecutionService.getDemoCourse().getCourseExecutionId()).stream()
                .flatMap(courseExecution -> courseExecution.getDifficultQuestions().stream())
                .collect(Collectors.toSet());

        difficultQuestionsToRemove.forEach(difficultQuestion -> {
            difficultQuestion.remove();
            difficultQuestionRepository.delete(difficultQuestion);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAssessments() {
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        this.assessmentRepository.findByExecutionCourseId(courseExecutionId)
                .stream()
                .forEach(assessment -> {
                    assessment.remove();
                    assessmentRepository.delete(assessment);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTopics() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();

        this.topicRepository.findTopics(courseId)
                .stream()
                .forEach(topic -> {
                    topic.remove();
                    this.topicRepository.delete(topic);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDiscussions() {
        List<Discussion> discussions = discussionRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        discussions.forEach(discussion -> {
            discussion.remove();
            discussionRepository.delete(discussion);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAnswers() {
        Set<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository.findDemoStudentQuestionAnswerItems();
        questionAnswerItemRepository.deleteAll(questionAnswerItems);

        Set<QuizAnswer> quizAnswers = quizAnswerRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        for (QuizAnswer quizAnswer : quizAnswers) {
            if (!quizAnswer.getQuiz().getId().equals(quiz2Keep) || !quizAnswer.getStudent().getUsername().equals(DemoUtils.STUDENT_USERNAME)) {
                quizAnswer.remove();
                quizAnswerRepository.delete(quizAnswer);
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        tournamentRepository.getTournamentsForCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .forEach(tournament -> {
                    tournament.getParticipants().forEach(user -> user.removeTournament(tournament));
                    if (tournament.getQuiz() != null) {
                        tournament.getQuiz().setTournament(null);
                    }

                    tournamentRepository.delete(tournament);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuizzes() {
        // remove quizzes except to keep
        quizRepository.findQuizzesOfExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .stream()
                .forEach(quiz -> {
                    if (!quiz.getId().equals(quiz2Keep)) {
                        quiz.remove();
                        this.quizRepository.delete(quiz);
                    }
                });

        // remove questions except to keep and that are not submitted
        for (Question question : questionRepository.findQuestions(courseExecutionService.getDemoCourse().getCourseId())
                .stream()
                .filter(question -> !questions2Keep.contains(question.getId()) && questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId()) == null)
                .collect(Collectors.toList())) {
            questionService.removeQuestion(question.getId());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuestionSubmissions() {
        questionSubmissionRepository.findQuestionSubmissionsByCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .forEach(questionSubmission -> {
                    questionSubmission.remove();
                    questionSubmissionRepository.delete(questionSubmission);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoStudents() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser() != null && user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .forEach(student -> {
                    if (student.getQuizAnswers().isEmpty()) {
                        student.remove();
                        this.userRepository.delete(student);
                    }
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void populateDemo() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        // Let's update the end date of the demo execution
        CourseExecution demoExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND));
        demoExecution.setEndDate(DateHandler.toLocalDateTime("2017-12-31T10:15:30+01:00[Europe/Lisbon]"));

        // Simulate login of demo teacher (this adds the demo teacher to the original demo execution )
        AuthDto authDemoTeacherDto = authUserService.demoTeacherAuth();

        // Get demo course and demo teacher
        Course demoCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND));
        User demoTeacher = userRepository.findById(authDemoTeacherDto.getUser().getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        // Create three additional course executions
        List<String> endDates = Arrays.asList(
                "2019-12-31T10:15:30+01:00[Europe/Lisbon]",
                "2022-12-31T10:15:30+01:00[Europe/Lisbon]",
                "2023-12-31T10:15:30+01:00[Europe/Lisbon]"
        );
        List<String> academicTerms = Arrays.asList("1st semester 2019/2020", "1st semester 2022/2023", "1st semester 2023/2024");

        for (int i = 0; i < endDates.size(); i++) {
            CourseExecution newCE = new CourseExecution(
                    demoCourse,
                    "Demo Course",
                    academicTerms.get(i),
                    Course.Type.TECNICO,
                    DateHandler.toLocalDateTime(endDates.get(i)));

            demoTeacher.addCourse(newCE);

            courseExecutionRepository.save(newCE);
        }
    }

}
