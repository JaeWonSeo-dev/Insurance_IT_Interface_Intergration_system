package com.portfolio.integration.dto;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceStatus;

public record InterfaceSearchCondition(
        String keyword,
        InterfaceStatus status,
        InterfaceChannelType channelType,
        Boolean active
) {
}
