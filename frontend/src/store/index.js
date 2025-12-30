import { defineStore } from 'pinia';

export const  userStore= defineStore('general',{
    state: () => ({
        user: {
            username: '',
            email: '',
            role: '',
            registerTime: null
        }
    })
})