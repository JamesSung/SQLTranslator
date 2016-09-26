# SQLTranslator
SQL Translator (JAVA Project)

This tool can change column names and table names those are contained in SQLs based on mapping rule.<br/>
Mapping Rules must be defined and saved as a file.<br/>
For example, SQL A below will be changed to SQL B.<br/>

 A) SELECT e.emp_id, e.emp_name, d.dept_id, d.dept_name, e.emp_date <br/>
      FROM emploee e, dept d <br/>
     WHERE e.dept_id = d.dept_id <br/>
       AND e.emp_id = ? <br/>
    
 B) SELECT e.emp_no, e.emp_nm, d.dept_no, d.dept_nm, e.emp_dt <br/>
      FROM emp01tb e, dept01tb d <br/>
     WHERE e.dept_no = d.dept_no <br/>
       AND e.emp_no = ? <br/>
 
 In this case mapping rule is <br/>
 
 old_table  old_column  new_table  new_column <br/>
 ============================================ <br/>
 emploee, emp_id,       emp01tb,   emp_no <br/>
 emploee, emp_name,     emp01tb,   emp_nm <br/>
 emploee, emp_date,     emp01tb,   emp_dt <br/>
 dept,    dept_id,      dept01tb,  dept_no <br/>
 dept,    emp_name,     dept01tb,  dept_nm <br/>
 
 This tool is also designed to handle inline views and subquerys.<br/>
 
 In the middle of developping process or for the ready to use solutions,<br/>
 you could change the naming rule of entities with this tool.<br/>
 I hope that it would be never happen to you.<br/>

 
 
    
    
   



