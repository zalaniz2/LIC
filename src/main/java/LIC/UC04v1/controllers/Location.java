package LIC.UC04v1.controllers;
/*
    This enum is where the areas where the general LIC clerkship areas are stored and pulled from.
    Altering this list will change the locations available across the board.
 */
public enum Location {
    None{
        public String toString(){
            return ("None");
        }
    },
    FortWorth{
        public String toString(){
            return ("Fort Worth");
        }
    },
    Denton{
        public String toString(){
            return ("Denton");
        }
    },
    Dallas{
        public String toString(){
            return ("Dallas");
        }
    },
    KellerSouthLakeAlliance{
        public String toString(){
            return ("Keller/Southlake/Alliance");
        }
    },
    Arlington {
        public String toString(){
            return ("Arlington");
        }
    },
    Mansfield {
        public String toString(){
            return ("Mansfield");
        }
    };

}
