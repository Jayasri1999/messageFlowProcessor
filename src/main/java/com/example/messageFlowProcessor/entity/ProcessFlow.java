package com.example.messageFlowProcessor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "process_flow")
@IdClass(ProcessFlowId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessFlow {
	 	@Id
	    @Column(name = "scenario")
	    private String scenario;

	    @Id
	    @Column(name = "country")
	    private String country;

	    @Id
	    @Column(name = "instance")
	    private String instance;

	    @Column(name = "entry_process")
	    private String entry_process;

	    @Column(name = "transform_process")
	    private String transform_process;

	    @Column(name = "exit_process")
	    private String exit_process;

	    @Column(name = "xslt_content")
	    private String xslt_content;


}
