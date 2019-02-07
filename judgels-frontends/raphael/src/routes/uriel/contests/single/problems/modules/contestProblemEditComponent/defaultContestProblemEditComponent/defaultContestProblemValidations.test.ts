import { DefaultValidProblemsSetData } from './defaultContestProblemValidations';

describe('contestProblemValidations', () => {
  test('ValidProblemsSetData', () => {
    expect(DefaultValidProblemsSetData('A,slug,OPEN,10')).toBeUndefined();
    expect(DefaultValidProblemsSetData('A,slug,OPEN,10\nB,slug2,CLOSED,20')).toBeUndefined();
    expect(DefaultValidProblemsSetData('A,slug,CLOSED,10')).toBeUndefined();
    expect(DefaultValidProblemsSetData('A,slug,OPEN')).toBeUndefined();
    expect(DefaultValidProblemsSetData('A,slug,CLOSED')).toBeUndefined();
    expect(DefaultValidProblemsSetData('A,slug')).toBeUndefined();
    expect(DefaultValidProblemsSetData('A')).toEqual('Each line must contain 2-4 comma-separated elements');
    expect(DefaultValidProblemsSetData('A,B,C,D,E')).toEqual('Each line must contain 2-4 comma-separated elements');
    expect(DefaultValidProblemsSetData('A A,slug')!.startsWith('Problem aliases: ')).toBeTruthy();
    expect(DefaultValidProblemsSetData('A,slug slug')!.startsWith('Problem slugs: ')).toBeTruthy();
    expect(DefaultValidProblemsSetData('A,slug,OPEN,-1')!.startsWith('Problem submissions limits: ')).toBeTruthy();
    expect(DefaultValidProblemsSetData('A,slug,ABANDONED')).toEqual('Problem statuses: Must be either OPEN or CLOSED');
    expect(DefaultValidProblemsSetData('A,slug\nA,slug2')).toEqual('Problem aliases must be unique');
    expect(DefaultValidProblemsSetData('A,slug\nB,slug')).toEqual('Problem slugs must be unique');
  });
});
