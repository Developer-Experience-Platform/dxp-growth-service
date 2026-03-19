package com.m_m.dxpgrowth.mapper;

import com.m_m.dxpgrowth.model.input.GoalTypeInput;
import com.m_m.dxpgrowth.model.output.GoalTypeResponse;
import com.m_m.dxpgrowth.persistence.entity.GoalTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalTypeMapper {

    GoalTypeEntity toEntity(GoalTypeInput input);

    GoalTypeResponse toResponse(GoalTypeEntity entity);

    List<GoalTypeResponse> toResponseList(List<GoalTypeEntity> entities);

    GoalTypeEntity updateEntityFromInput(GoalTypeInput input, @MappingTarget GoalTypeEntity entity);
}
