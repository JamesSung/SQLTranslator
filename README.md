# SQLTranslator
SQL Translator (JAVA Project)

This tool can change column names and table names those are contained in SQLs based on mapping rule.
Mapping Rules must be defined and saved as a file.
For example, SQL A below will be changed to SQL B.

 A) SELECT e.emp_id, e.emp_name, d.dept_id, d.dept_name, e.emp_date
      FROM emploee e, dept d
     WHERE e.dept_id = d.dept_id
       AND e.emp_id = ?
    
 B) SELECT e.emp_no, e.emp_nm, d.dept_no, d.dept_nm, e.emp_dt
      FROM emp01tb e, dept01tb d
     WHERE e.dept_no = d.dept_no
       AND e.emp_no = ?
 
 In this case mapping rule is 
 
 old_table  old_column  new_table  new_column
 ============================================
 emploee, emp_id,       emp01tb,   emp_no
 emploee, emp_name,     emp01tb,   emp_nm
 emploee, emp_date,     emp01tb,   emp_dt
 dept,    dept_id,      dept01tb,  dept_no
 dept,    emp_name,     dept01tb,  dept_nm
 
 This tool is also designed to handle inline views and subquerys.
 
 In the middle of developping process or for the ready to use solutions,
 you could change the naming rule of entities with this tool.
 I hope that it would be never happen to you.

 
 
    
    
   



