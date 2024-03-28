<template>
  <div class="container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number :number="teacherDashboard.studentStatsDto[0].numStudents" />
        </div>
        <div class="project-name">
          <p>Number of Students</p>
        </div>
      </div>
      <div class="items">
        <div ref="numStudentsWhoSolved75MoreQuestions" class="icon-wrapper">
          <animated-number :number="teacherDashboard.studentStatsDto[0].numMore75CorrectQuestions" />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved more then 75 % Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="numStudentsWhoSolvedAtLeast3Quizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.studentStatsDto[0].numAtLeast3Quizzes" />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved at least 3 Quizzes</p>
        </div>
      </div>
    </div>
      <h2>Comparison with previous course executions</h2>
        <div v-if="teacherDashboard != null" class="stats-container">
          <div class="bar-chart">
            <Bar :chartData="studentStats" :chartOptions="chartOptions" />
        </div>
      </div>
    </div>
  </template>



<script lang="ts">
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';

import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/teacherDashboard/TeacherDashboard.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
  )

@Component({components: { AnimatedNumber}}) 

export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  chartOptions() {
    return {
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  studentStats() {
    return {
      labels: [
        this.teacherDashboard?.studentStatsDto[2]
          ? this.teacherDashboard?.studentStatsDto[2].courseExecutionYear
          : '',
        this.teacherDashboard?.studentStatsDto[1]
          ? this.teacherDashboard?.studentStatsDto[1].courseExecutionYear
          : '',
        this.teacherDashboard?.studentStatsDto[0].courseExecutionYear + ' (current)',
      ],
      datasets: [
        {
          label: 'Total Number of Students',
          backgroundColor: '#f87109',
          data: [
            this.teacherDashboard?.studentStatsDto[2]
              ? this.teacherDashboard?.studentStatsDto[2].numStudents
              : 0,
            this.teacherDashboard?.studentStatsDto[1].numStudents
              ? this.teacherDashboard?.studentStatsDto[1].numStudents
              : 0,
            this.teacherDashboard?.studentStatsDto[0].numStudents,
          ],
        },
        {
          label: 'Students who Solved at least 75% Questions',
          backgroundColor: '#f87979',
          data: [
            this.teacherDashboard?.studentStatsDto[2]
              ? this.teacherDashboard?.studentStatsDto[2].numMore75CorrectQuestions
              : 0,
            this.teacherDashboard?.studentStatsDto[1]
              ? this.teacherDashboard?.studentStatsDto[1].numMore75CorrectQuestions
              : 0,
            this.teacherDashboard?.studentStatsDto[0].numMore75CorrectQuestions,
          ],
        },
        {
          label: 'Students who Solved at least 3 Quizzes',
          backgroundColor: '#f11119',
          data: [
            this.teacherDashboard?.studentStatsDto[2]
              ? this.teacherDashboard?.studentStatsDto[2].numAtLeast3Quizzes
              : 0,
            this.teacherDashboard?.studentStatsDto[1]
              ? this.teacherDashboard?.studentStatsDto[1].numAtLeast3Quizzes
              : 0,
            this.teacherDashboard?.studentStatsDto[0].numAtLeast3Quizzes,
          ],
        },
      ],
    };
  }
}

</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.90);
    height: 400px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
