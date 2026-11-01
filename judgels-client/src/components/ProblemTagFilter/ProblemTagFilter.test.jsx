import { act, render, screen } from '@testing-library/react';

import { problemAllTagsQueryOptions } from '../../modules/queries/problemTag';
import { QueryClientProviderWrapper } from '../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../test/RouterWrapper';
import { nockApi } from '../../utils/nock';
import ProblemTagFilter from './ProblemTagFilter';

describe('ProblemTagFilter', () => {
  test('checks the tags in the URL', async () => {
    nockApi()
      .get('/v4/problems/tags')
      .reply(200, {
        data: [
          {
            title: 'Visibility',
            options: [
              { label: 'private', value: 'visibility-private', count: 2 },
              { label: 'public', value: 'visibility-public', count: 0 },
            ],
          },
        ],
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problems?tags=visibility-private']}>
            <ProblemTagFilter queryOptions={problemAllTagsQueryOptions()} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    expect(await screen.findByRole('checkbox', { name: 'private (2)' })).toBeChecked();
    expect(screen.getByRole('checkbox', { name: 'public (0)' })).not.toBeChecked();
  });
});
