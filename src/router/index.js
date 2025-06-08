import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Volunteer_Dashboard.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Volunteer_Home.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Volunteer_ProfileEdit.vue')
      },
      {
        path: 'reviews',
        name: 'Reviews',
        component: () => import('../views/Volunteer_Evaluate.vue')
      },
      {
        path: 'complaints',
        name: 'Complaints',
        component: () => import('../views/Volunteer_Complaint.vue')
      },
      {
        path: 'training',
        name: 'Training',
        component: () => import('../views/Volunteer_Trainings.vue')
      },
      {
        path: 'projects',
        name: 'Projects',
        component: () => import('../views/Volunteer_Projects.vue')
      },
      {
        path: 'project-apply',
        name: 'ProjectApply',
        component: () => import('../views/Volunteer_PendingProjects.vue')
      },
      {
        path: 'teams',
        name: 'Teams',
        component: () => import('../views/Volunteer_Teams.vue')
      },
      {
        path: 'teams-more',
        name: 'TeamsMore',
        component: () => import('../views/Volunteer_TeamsMore.vue')
      },
      {
        path: 'projects-more',
        name: 'ProjectsMore',
        component: () => import('../views/Volunteer_ProjectsMore.vue')
      },
      {
        path: 'project-detail/:id',
        name: 'ProjectDetail',
        component: () => import('../views/Volunteer_ProjectDetail.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 