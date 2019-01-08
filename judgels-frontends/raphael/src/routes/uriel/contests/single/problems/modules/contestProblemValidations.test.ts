import { ValidProblemsSetData } from './contestProblemValidations';

describe('contestProblemValidations', () => {
  test('ValidProblemsSetData', () => {
    expect(ValidProblemsSetData('A,slug,OPEN,10')).toBeUndefined();
    expect(ValidProblemsSetData('A,slug,OPEN,10\nB,slug2,CLOSED,20')).toBeUndefined();
    expect(ValidProblemsSetData('A,slug,CLOSED,10')).toBeUndefined();
    expect(ValidProblemsSetData('A,slug,OPEN')).toBeUndefined();
    expect(ValidProblemsSetData('A,slug,CLOSED')).toBeUndefined();
    expect(ValidProblemsSetData('A,slug')).toBeUndefined();
    expect(ValidProblemsSetData('A')).toEqual('Each line must contain 2-4 comma-separated elements');
    expect(ValidProblemsSetData('A,B,C,D,E')).toEqual('Each line must contain 2-4 comma-separated elements');
    expect(ValidProblemsSetData('A A,slug')!.startsWith('Problem aliases: ')).toBeTruthy();
    expect(ValidProblemsSetData('A,slug slug')!.startsWith('Problem slugs: ')).toBeTruthy();
    expect(ValidProblemsSetData('A,slug,OPEN,-1')!.startsWith('Problem submissions limits: ')).toBeTruthy();
    expect(ValidProblemsSetData('A,slug,ABANDONED')).toEqual('Problem statuses: Must be either OPEN or CLOSED');
    expect(ValidProblemsSetData('A,slug\nA,slug2')).toEqual('Problem aliases must be unique');
    expect(ValidProblemsSetData('A,slug\nB,slug')).toEqual('Problem slugs must be unique');
  });
});
