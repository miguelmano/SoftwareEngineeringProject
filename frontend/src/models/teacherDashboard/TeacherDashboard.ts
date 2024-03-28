import TeacherDashboardStudentStats from "@/models/teacherDashboard/TeacherDashboardStudentStats";

export default class TeacherDashboard {
  id!: number;
  studentStatsDto: TeacherDashboardStudentStats[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;

      if (jsonObj.studentStatsDto) {
        this.studentStatsDto = jsonObj.studentStatsDto.map(
          (studentStats: TeacherDashboardStudentStats) => new TeacherDashboardStudentStats(studentStats)
        );
      }
    }
  }
}
