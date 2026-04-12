package com.alpharedge.mapper;

import com.alpharedge.document.Holding;
import com.alpharedge.document.Portfolio;
import com.alpharedge.dto.response.HoldingSummaryDTO;
import com.alpharedge.dto.response.PortfolioDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    PortfolioDTO toDTO(Portfolio portfolio);

    Portfolio toEntity(PortfolioDTO portfolioDTO);

    HoldingSummaryDTO toDTO(Holding holding);

    Holding toEntity(HoldingSummaryDTO holdingSummaryDTO);
}
