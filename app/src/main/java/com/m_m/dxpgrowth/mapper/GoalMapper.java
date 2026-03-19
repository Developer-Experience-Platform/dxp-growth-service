package com.m_m.dxpgrowth.mapper;

import com.m_m.dxpgrowth.model.input.GoalInput;
import com.m_m.dxpgrowth.model.output.GoalResponse;
import com.m_m.dxpgrowth.persistence.entity.GoalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalTypeMapper.class})
public interface GoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "dataHoraCriacao", ignore = true)
    @Mapping(target = "dataHoraAtualizacao", ignore = true)
    GoalEntity toEntity(GoalInput input);

    @Mapping(target = "tipo", source = "tipo")
    GoalResponse toResponse(GoalEntity entity);

    List<GoalResponse> toResponseList(List<GoalEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "dataHoraCriacao", ignore = true)
    @Mapping(target = "dataHoraAtualizacao", ignore = true)
    GoalEntity updateEntityFromInput(GoalInput input, @MappingTarget GoalEntity entity);
}
