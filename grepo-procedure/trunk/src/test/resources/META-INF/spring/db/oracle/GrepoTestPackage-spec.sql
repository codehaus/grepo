CREATE OR REPLACE PACKAGE grepo_test
IS
  PROCEDURE simple_proc1 (
     p_string IN VARCHAR2,
     p_integer IN INTEGER,
     p_result OUT VARCHAR2);

  PROCEDURE simple_proc2 (
  	p_string IN VARCHAR2,
  	p_integer IN OUT INTEGER);

  FUNCTION simple_function (
    p_string IN VARCHAR2,
    p_integer IN INTEGER)
    RETURN VARCHAR2;

END grepo_test;

