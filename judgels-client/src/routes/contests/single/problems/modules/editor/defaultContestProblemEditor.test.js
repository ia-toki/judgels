import defaultContestProblemEditor from './defaultContestProblemEditor';

describe('defaultContestProblemEditor', () => {
  test('validator', () => {
    expect(defaultContestProblemEditor.validator('A,slug,OPEN,10')).toBeUndefined();
    expect(defaultContestProblemEditor.validator('A,slug,OPEN,10\nB,slug2,CLOSED,20')).toBeUndefined();
    expect(defaultContestProblemEditor.validator('A,slug,CLOSED,10')).toBeUndefined();
    expect(defaultContestProblemEditor.validator('A,slug,OPEN')).toBeUndefined();
    expect(defaultContestProblemEditor.validator('A,slug,CLOSED')).toBeUndefined();
    expect(defaultContestProblemEditor.validator('A,slug')).toBeUndefined();
    expect(defaultContestProblemEditor.validator('A')).toEqual('Each line must contain 2-4 comma-separated elements');
    expect(defaultContestProblemEditor.validator('A,B,C,D,E')).toEqual(
      'Each line must contain 2-4 comma-separated elements'
    );
    expect(defaultContestProblemEditor.validator('A A,slug').startsWith('Problem aliases: ')).toBeTruthy();
    expect(defaultContestProblemEditor.validator('A,slug slug').startsWith('Problem slugs: ')).toBeTruthy();
    expect(
      defaultContestProblemEditor.validator('A,slug,OPEN,-1').startsWith('Problem submissions limits: ')
    ).toBeTruthy();
    expect(defaultContestProblemEditor.validator('A,slug,ABANDONED')).toEqual(
      'Problem statuses: Must be either OPEN or CLOSED'
    );
    expect(defaultContestProblemEditor.validator('A,slug\nA,slug2')).toEqual('Problem aliases must be unique');
    expect(defaultContestProblemEditor.validator('A,slug\nB,slug')).toEqual('Problem slugs must be unique');
  });
});
