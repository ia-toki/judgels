export interface ProblemProgress {
  verdict: string;
  score: number;
}

export interface ProblemStats {
  totalScores: number;
  totalUsersAccepted: number;
  totalUsersTried: number;
}

export interface ProblemTopStatsEntry {
  userJid: string;
  stats: number;
}

export interface ProblemTopStats {
  topUsersByScore: ProblemTopStatsEntry[];
  topUsersByTime: ProblemTopStatsEntry[];
  topUsersByMemory: ProblemTopStatsEntry[];
}
