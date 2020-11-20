import gcjContestProblemEditor from './gcjContestProblemEditor';

describe('contestProblemValidations', () => {
  test('ValidProblemsSetData', () => {
    expect(gcjContestProblemEditor.validator('A,slug,OPEN,10').startsWith('Problem points: ')).toBeTruthy();
    expect(gcjContestProblemEditor.validator('A,slug,0,OPEN,10')).toBeUndefined();
    expect(gcjContestProblemEditor.validator('A,slug,3,OPEN,10\nB,slug2,2,CLOSED,20')).toBeUndefined();
    expect(gcjContestProblemEditor.validator('A,slug,1,CLOSED,10')).toBeUndefined();
    expect(gcjContestProblemEditor.validator('A,slug,2,OPEN')).toBeUndefined();
    expect(gcjContestProblemEditor.validator('A,slug,3,CLOSED')).toBeUndefined();
    expect(gcjContestProblemEditor.validator('A,slug,4')).toBeUndefined();
    expect(gcjContestProblemEditor.validator('A')).toEqual('Each line must contain 3-5 comma-separated elements');
    expect(gcjContestProblemEditor.validator('A,B,C,D,E,F')).toEqual(
      'Each line must contain 3-5 comma-separated elements'
    );
    expect(gcjContestProblemEditor.validator('A A,slug,1').startsWith('Problem aliases: ')).toBeTruthy();
    expect(gcjContestProblemEditor.validator('A,slug slug,1').startsWith('Problem slugs: ')).toBeTruthy();
    expect(
      gcjContestProblemEditor.validator('A,slug,1,OPEN,-1').startsWith('Problem submissions limits: ')
    ).toBeTruthy();
    expect(gcjContestProblemEditor.validator('A,slug,1,ABANDONED')).toEqual(
      'Problem statuses: Must be either OPEN or CLOSED'
    );
    expect(gcjContestProblemEditor.validator('A,slug,1\nA,slug2,2')).toEqual('Problem aliases must be unique');
    expect(gcjContestProblemEditor.validator('A,slug,1\nB,slug,2')).toEqual('Problem slugs must be unique');
  });
});
