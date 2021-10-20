package com.trg.fms.service.c.heatbeat;

import com.trg.fms.api.Penalty;
import com.trg.fms.service.c.repo.PenaltyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KafkaPenaltyConsumerTest {

    @MockBean
    private PenaltyRepository penaltyRepository;

    //We don't need this bean hear, but we need to mock it to avoid db init during test
    @MockBean
    private ConnectionFactoryInitializer initializer;

    @Autowired
    private KafkaPenaltyConsumer kafkaPenaltyConsumer;

    @Captor
    private ArgumentCaptor<Penalty> penaltyArgumentCaptor;

    /**
     * Need more tests to cover
     */
    @Test
    public void testListenWhenDriverExistCurrentPointUpdated() {

        Long driverId = 1L;
        Penalty existingPenalty = new Penalty();
        existingPenalty.setId(1L);
        existingPenalty.setDriverId(driverId);
        existingPenalty.setTotalPoints(10L);

        Mockito.when(penaltyRepository.findByDriverId(driverId)).thenReturn(Mono.just(existingPenalty));

        //Not important. Avoid exceptions
        Mockito.when(penaltyRepository.save(Mockito.any(Penalty.class))).thenReturn(Mono.just(existingPenalty));
        kafkaPenaltyConsumer.listen(15L, driverId);
        Mockito.verify(penaltyRepository).save(penaltyArgumentCaptor.capture());
        Penalty actualPenalty =  penaltyArgumentCaptor.getValue();
        assertEquals(15L, actualPenalty.getTotalPoints());
    }

}