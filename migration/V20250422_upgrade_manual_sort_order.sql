alter table youth_info add column sort_order int null after entrepreneurship_demand;

alter table enterprise_info add column sort_order int null after description;

alter table job_post add column sort_order int null after job_description;

alter table policy_article add column sort_order int null after content_html;
