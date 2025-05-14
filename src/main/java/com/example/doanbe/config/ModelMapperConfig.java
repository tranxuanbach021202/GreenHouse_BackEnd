package com.example.doanbe.config;

import com.example.doanbe.document.ProjectMember;
import com.example.doanbe.payload.response.ProjectMemberResponse;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        Converter<ProjectMember, ProjectMemberResponse> memberToResponseConverter =
                new Converter<ProjectMember, ProjectMemberResponse>() {
                    @Override
                    public ProjectMemberResponse convert(MappingContext<ProjectMember, ProjectMemberResponse> context) {
                        ProjectMember source = context.getSource();
                        ProjectMemberResponse destination = new ProjectMemberResponse();
                        destination.setUserId(source.getUserId());
                        destination.setUserName(source.getUserName());
                        destination.setEmail(source.getEmail());
                        destination.setUrlAvatar(source.getUrlAvatar());
                        destination.setRole(source.getRole().name());
                        destination.setDisplayName(source.getUserName());
                        return destination;
                    }
                };

        return modelMapper;
    }
}


