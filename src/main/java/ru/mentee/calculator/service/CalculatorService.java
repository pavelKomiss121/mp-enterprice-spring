package ru.mentee.calculator.service;

import org.springframework.stereotype.Service;
import ru.mentee.calculator.annotation.Loggable;
import ru.mentee.calculator.annotation.Timed;

@Service
public class CalculatorService {

  @Loggable
  @Timed
  public int add(int a, int b) {
    return a + b;
  }

  @Loggable
  @Timed
  public int subtract(int a, int b) {
    return a - b;
  }

  @Loggable
  @Timed
  public int multiply(int a, int b) {
    return a * b;
  }

  @Loggable
  @Timed
  public int divide(int a, int b) {
    if (b == 0) {
      throw new IllegalArgumentException("Division by zero!");
    }
    return a / b;
  }
}