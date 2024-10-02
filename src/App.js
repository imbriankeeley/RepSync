import React, { useState, useEffect } from 'react';
import WorkoutLogger from './components/WorkoutLogger';
import CustomWorkout from './components/CustomWorkout';
import WorkoutCalendar from './components/WorkoutCalendar';
import CustomWorkouts from './components/CustomWorkouts';

const App = () => {
  const [workouts, setWorkouts] = useState([]);
  const [customWorkouts, setCustomWorkouts] = useState([]);

  useEffect(() => {
    const savedWorkouts = JSON.parse(localStorage.getItem('workouts')) || [];
    const savedCustomWorkouts = JSON.parse(localStorage.getItem('customWorkouts')) || [];
    setWorkouts(savedWorkouts);
    setCustomWorkouts(savedCustomWorkouts);
  }, []);

  useEffect(() => {
    localStorage.setItem('workouts', JSON.stringify(workouts));
  }, [workouts]);

  useEffect(() => {
    localStorage.setItem('customWorkouts', JSON.stringify(customWorkouts));
  }, [customWorkouts]);

  const addWorkout = (workout) => {
    setWorkouts([...workouts, workout]);
  };

  const addCustomWorkout = (customWorkout) => {
    setCustomWorkouts([...customWorkouts, customWorkout]);
  };

  return (
    <div>
      <h1>Fitness Notes</h1>
      <WorkoutLogger addWorkout={addWorkout} />
      <CustomWorkout addCustomWorkout={addCustomWorkout} />
      <CustomWorkouts customWorkouts={customWorkouts} />
      <WorkoutCalendar workouts={workouts} />
    </div>
  );
};

export default App;