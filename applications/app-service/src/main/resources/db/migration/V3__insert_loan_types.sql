INSERT INTO loan_types (code, name, min_amount, max_amount, interest_rate, auto_approval) VALUES
                                                                                            ('personal_loan', 'Préstamo personal', 1000.00, 20000.00, 0.13, true),
                                                                                            ('mortgage_loan', 'Préstamo hipotecario', 20000.00, 300000.00, 0.7, false),
                                                                                            ('car_loan', 'Préstamo vehicular', 5000.00, 100000.00, 0.98, true),
                                                                                            ('student_loan', 'Préstamo estudiantil', 1000.00, 50000.00, 0.55, false),
                                                                                            ('business_loan', 'Préstamo empresarial', 10000.00, 500000.00, 15, false);
