import {
  Chat,
  FolderClose,
  History,
  Key,
  Layers,
  Link,
  Manual,
  Notifications,
  People,
  Presentation,
  Properties,
  TakeAction,
  Th,
} from '@blueprintjs/icons';
import { ContestTab } from '../../../../../modules/api/uriel/contestWeb';

export const contestIcon = {
  [ContestTab.Overview]: <Properties />,
  [ContestTab.Announcements]: <Notifications />,
  [ContestTab.Problems]: <Manual />,
  [ContestTab.Editorial]: <Presentation />,
  [ContestTab.Contestants]: <People />,
  [ContestTab.Supervisors]: <TakeAction />,
  [ContestTab.Managers]: <Key />,
  [ContestTab.Teams]: <Link />,
  [ContestTab.Submissions]: <Layers />,
  [ContestTab.Clarifications]: <Chat />,
  [ContestTab.Scoreboard]: <Th />,
  [ContestTab.Files]: <FolderClose />,
  [ContestTab.Logs]: <History />,
};
