package pl.coderslab.twojtrening.plantraining;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderslab.twojtrening.error.AccessUserException;
import pl.coderslab.twojtrening.error.NotFoundException;
import pl.coderslab.twojtrening.plan.Plan;
import pl.coderslab.twojtrening.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class PlanTrainingServiceTest {
    @Mock
    PlanTrainingRepository planTrainingRepository;
    @InjectMocks
    PlanTrainingService underTest;
    @Captor
    ArgumentCaptor<PlanTraining> planTrainingArgumentCaptor;

    @Test
    void shouldAddTrainingToPlan() {
        //given
        PlanTraining planTraining = PlanTraining.builder().id(1L).build();
        //when
        underTest.addTrainingToPlan(planTraining);
        //then
        verify(planTrainingRepository).save(planTrainingArgumentCaptor.capture());
        PlanTraining capturedPlanTraining = planTrainingArgumentCaptor.getValue();
        assertThat(capturedPlanTraining).isEqualTo(planTraining);
    }

    @Test
    void shouldDeleteTrainingFromPlan() {
        //given
        long id = 1L;
        User user = User.builder().id(id).build();
        Plan plan = Plan.builder()
                .id(id).user(user)
                .build();
        PlanTraining planTraining = PlanTraining.builder()
                .id(id).plan(plan)
                .build();
        given(planTrainingRepository.findById(id)).willReturn(Optional.of(planTraining));
        //when
        underTest.deleteTrainingFromPlan(id,user);
        //then
        verify(planTrainingRepository).findById(id);
        verify(planTrainingRepository).deleteById(id);
    }
    @Test
    void shouldNotDeleteTrainingFromPlanWrongId() {
        //given
        long id = 1L;
        User user = User.builder().id(id).build();
        given(planTrainingRepository.findById(id)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteTrainingFromPlan(id, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Training in plan with id:%s not found", id));
        verify(planTrainingRepository,never()).deleteById(any());
    }
    @Test
    void shouldNotDeleteTrainingFromPlanWrongUser() {
        //given
        long id = 1L;
        User user = User.builder().id(id).build();
        User wrongUser = User.builder().id(2L).build();
        Plan plan = Plan.builder()
                .id(id).user(user)
                .build();
        PlanTraining planTraining = PlanTraining.builder()
                .id(id).plan(plan)
                .build();
        given(planTrainingRepository.findById(id)).willReturn(Optional.of(planTraining));
        //when
        assertThatThrownBy(() -> underTest.deleteTrainingFromPlan(id, wrongUser))
                .isInstanceOf(AccessUserException.class)
                .hasMessageContaining("Access forbidden");
        verify(planTrainingRepository,never()).deleteById(any());
    }

    @Test
    void shouldGetSinglePlanWithTrainingsById() {
        //given
        long id = 1L;
        User user = User.builder().id(id).build();
        Plan plan = Plan.builder()
                .id(id).user(user)
                .build();
        PlanTraining planTraining = PlanTraining.builder()
                .id(id).plan(plan)
                .build();
        given(planTrainingRepository.findById(id)).willReturn(Optional.of(planTraining));
        //when
        PlanTraining singlePlanWithTrainingsById = underTest.getSinglePlanWithTrainingsById(id, user);
        //then
        verify(planTrainingRepository).findById(id);
        assertThat(singlePlanWithTrainingsById).isEqualTo(planTraining);
    }
    @Test
    void shouldGetSinglePlanWithTrainingsByIdWrongId() {
        //given
        long id = 1L;
        User user = User.builder().id(id).build();
        given(planTrainingRepository.findById(id)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.getSinglePlanWithTrainingsById(id, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Training in plan with id:%s not found", id));
    }
    @Test
    void shouldGetSinglePlanWithTrainingsByIdWrongUser() {
        //given
        long id = 1L;
        User user = User.builder().id(id).build();
        User wrongUser = User.builder().id(2L).build();
        Plan plan = Plan.builder()
                .id(id).user(user)
                .build();
        PlanTraining planTraining = PlanTraining.builder()
                .id(id).plan(plan)
                .build();
        given(planTrainingRepository.findById(id)).willReturn(Optional.of(planTraining));
        //when
        //then
        assertThatThrownBy(() -> underTest.getSinglePlanWithTrainingsById(id, wrongUser))
                .isInstanceOf(AccessUserException.class)
                .hasMessageContaining("Access forbidden");
    }
}