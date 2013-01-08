class Grammar:
    
    # noun contexts
    def pluralize(word):
        word = word.strip()
    
        # irregular cases
        if word.lower() == "goose":
            word = "geese"
    
        elif word.lower() == "mouse":
            word = "mice"
    
        elif word.lower() == "sheep"
            word = "sheep"
    
        elif word.lower() == "moose"
            word = "moose"
    
        elif word.lower() == "man"
            word = "men"
    
        elif word.lower() == "woman"
            word = "women"
    
        elif word.lower() == "person"
            word = "people"
            
        else:
            if word.lower().endswith("y"):
                word = word[:-1] + "ies"
    
            if word.lower().endswith("s"):
                word = word[:-1] + "es"
                
            if word.lower().endswith("f"):
                word = word[:-1] + "ves"
    
            if word.lower().endswith("fe"):
                word = word[:-2] + "ves"
                
            if word.lower().endswith("us"):
                word = word[:-2] + "i"
                
            else:
                word += "s"
            
        return word
        
    
    
    # verb contexts
    def ing(word):
        word = word.strip()
        
        # irregular cases
        #if word.lower() = "":
            #word = ""
            
        else:
            if word.lower().endswith("ie"):
                word = word[:-2] + "ying"
            
            if word.lower().endswith("ot"):
                word = word[:-2] + "eting"
                
            else:
                word += "ing"
        
        return word
