package ru.krasnov.jetbrains.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.krasnov.jetbrains.dto.api.BuildResultsRequest;
import ru.krasnov.jetbrains.model.BuildTracker;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BuildResultsMapper {

    BuildResultsRequest mapFromModel(BuildTracker buildTracker);
}
