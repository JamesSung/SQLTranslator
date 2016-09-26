# SQLTranslator
SQL Translator (JAVA Project)

This tool can change comumn names and table names that are contained in SQL based on mapping rule.
Mapping Rules must be defined and saved as a file.
For example, from SQL A to B
 A) select e.emp_id, e.emp_name, d.dept_id, d.dept_name, e.emp_date
      from emploee e, dept d
     where e.dept_id = d.dept_id
       and e.emp_id = ?
    
 B) select e.emp_no, e.emp_nm, d.dept_no, d.dept_nm, e.emp_dt
      from emp01tb e, dept01tb d
     where e.dept_no = d.dept_no
       and e.emp_no = ?
 
 In this case mapping rule is 
 
 old_table,old_column,new_table,new_column
 =========================================
 emploee,emp_id,emp01tb,emp_no
 emploee,emp_name,emp01tb,emp_nm
 emploee,emp_date,emp01tb,emp_dt
 dept,dept_id,dept01tb,dept_no
 dept,emp_name,dept01tb,dept_nm
 
 this tool is also desined to handle inline views and subquerys.
 
 In the middle of developping process or for the ready to use solutions,
 you can change your naming rule of entities with this tool.
 I hope that it would be never happen to you.

 
 
    
    
   



